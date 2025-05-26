package com.nexos.api_inventory.util.jpafilter;

import io.github.perplexhub.rsql.RSQLJPASupport;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.BinaryOperator;

@Component
public class JpaFilterHelper {

    public <T> Specification<T> toSpecification(String filters) {
        return RSQLJPASupport.toSpecification(filters);
    }


    public <T> Specification<T> doOnEvery(BinaryOperator<Specification<Object>> operation, String... filters) {
        return pipe(
                Arrays.stream(filters)
                        .map(RSQLJPASupport::toSpecification)
                        .reduce(operation)
                        .orElseThrow()
        );
    }

    @SuppressWarnings("unchecked")
    public <T> Specification<T> pipe(Specification<Object> specification) {
        return (Specification<T>) specification;
    }

}