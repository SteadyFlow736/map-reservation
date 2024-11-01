package org.example.mapreservation.hairshop.infrastructure;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.mapreservation.hairshop.application.repository.HairShopQueryRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.domain.HairShopSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.example.mapreservation.hairshop.domain.QHairShop.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
@Repository
public class HairShopQueryRepositoryImpl implements HairShopQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 검색어에 일치하는 헤어샵을 리턴한다.
     *
     * @param searchCondition 검색어 조건
     * @param pageable        페이징, 정렬 기준
     * @return 페이징된 결과
     */
    public Page<HairShop> search(HairShopSearchCondition searchCondition, Pageable pageable) {
        BooleanBuilder where = getWhere(searchCondition);

        JPAQuery<HairShop> query = queryFactory
                .selectFrom(hairShop)
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        for (Sort.Order o : pageable.getSort()) {
            PathBuilder<HairShop> pathBuilder = new PathBuilder<>(hairShop.getType(), hairShop.getMetadata());
            query.orderBy(
                    new OrderSpecifier(
                            o.isAscending() ? Order.ASC : Order.DESC,
                            pathBuilder.get(o.getProperty())
                    )
            );
        }
        List<HairShop> hairShops = query.fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(hairShop.count())
                .from(hairShop)
                .where(where);

        return PageableExecutionUtils.getPage(hairShops, pageable, countQuery::fetchOne);
    }

    private BooleanBuilder getWhere(HairShopSearchCondition searchCondition) {
        BooleanBuilder builder = new BooleanBuilder();
        builder
                .and(hairShopNameLike(searchCondition.searchTerm()))
                .and(minLongitude(searchCondition.minLongitude()))
                .and(maxLongitude(searchCondition.maxLongitude()))
                .and(minLatitude(searchCondition.minLatitude()))
                .and(maxLatitude(searchCondition.maxLatitude()));
        return builder;
    }

    private BooleanExpression hairShopNameLike(String hairShopName) {
        return isEmpty(hairShopName) ? null : hairShop.name.contains(hairShopName);
    }

    private BooleanExpression minLongitude(String longitude) {
        return isEmpty(longitude) ? null : hairShop.longitude.goe(longitude);
    }

    private BooleanExpression maxLongitude(String longitude) {
        return isEmpty(longitude) ? null : hairShop.longitude.loe(longitude);
    }

    private BooleanExpression minLatitude(String latitude) {
        return isEmpty(latitude) ? null : hairShop.latitude.goe(latitude);
    }

    private BooleanExpression maxLatitude(String latitude) {
        return isEmpty(latitude) ? null : hairShop.latitude.loe(latitude);
    }
}
