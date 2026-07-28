package com.reserveflow.bookingprovider.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reserveflow.bookingprovider.entity.BookingProvider;
import com.reserveflow.bookingprovider.entity.BookingProviderStatus;
import com.reserveflow.bookingprovider.entity.QBookingProvider;
import com.reserveflow.reservationrequest.dto.BookingProviderType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;

/**
 * {@link BookingProviderRepositoryCustom}의 QueryDSL 구현.
 */
@RequiredArgsConstructor
public class BookingProviderRepositoryImpl implements BookingProviderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<BookingProvider> search(BookingProviderType providerType, String location, Integer partySize,
            Pageable pageable) {
        QBookingProvider provider = QBookingProvider.bookingProvider;

        // ACTIVE 제공자만 대상으로 하고, 전달된 필터만 조건에 추가한다(null 필터는 SQL에서 제외).
        BooleanBuilder where = new BooleanBuilder();
        where.and(provider.status.eq(BookingProviderStatus.ACTIVE));
        if (providerType != null) {
            where.and(provider.providerType.eq(providerType));
        }
        if (StringUtils.hasText(location)) {
            where.and(provider.locationText.containsIgnoreCase(location));
        }
        if (partySize != null) {
            where.and(provider.maxPartySize.goe(partySize));
        }

        // 다음 페이지 존재 여부만 필요하므로 size + 1건을 조회해 초과분으로 hasNext를 판단한다.
        int pageSize = pageable.getPageSize();
        List<BookingProvider> rows = queryFactory
                .selectFrom(provider)
                .where(where)
                .orderBy(provider.id.asc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1L)
                .fetch();

        boolean hasNext = rows.size() > pageSize;
        List<BookingProvider> content = hasNext ? rows.subList(0, pageSize) : rows;
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
