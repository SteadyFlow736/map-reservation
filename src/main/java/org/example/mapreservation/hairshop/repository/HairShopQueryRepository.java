package org.example.mapreservation.hairshop.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.dto.HairShopDto;
import org.example.mapreservation.hairshop.dto.HairShopSearchCondition;
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
public class HairShopQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 검색어에 일치하는 헤어샵을 리턴한다.
     *
     * @param searchCondition 검색어 조건
     * @param pageable        페이징, 정렬 기준
     * @return 페이징된 결과
     */
    public Page<HairShopDto> search(HairShopSearchCondition searchCondition, Pageable pageable) {
        JPAQuery<HairShop> query = queryFactory
                .selectFrom(hairShop)
                .where(
                        hairShopNameLikeExpression(searchCondition.searchTerm())
                )
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
                .where(
                        hairShopNameLikeExpression(searchCondition.searchTerm())
                );

        Page<HairShop> page = PageableExecutionUtils.getPage(hairShops, pageable, countQuery::fetchOne);
        return page.map(HairShopDto::from);
    }

    private BooleanExpression hairShopNameLikeExpression(String hairShopName) {
        return isEmpty(hairShopName) ? null : hairShop.name.contains(hairShopName);
    }
}
