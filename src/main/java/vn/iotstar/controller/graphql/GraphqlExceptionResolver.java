package vn.iotstar.controller.graphql;

import java.util.NoSuchElementException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

@Component
public class GraphqlExceptionResolver extends DataFetcherExceptionResolverAdapter {
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof NoSuchElementException)
            return GraphqlErrorBuilder.newError(env).errorType(ErrorType.NOT_FOUND).message(ex.getMessage()).build();
        if (ex instanceof IllegalArgumentException || ex instanceof ConstraintViolationException)
            return GraphqlErrorBuilder.newError(env).errorType(ErrorType.BAD_REQUEST).message(ex.getMessage()).build();
        if (ex instanceof DataIntegrityViolationException)
            return GraphqlErrorBuilder.newError(env).errorType(ErrorType.BAD_REQUEST)
                    .message("Dữ liệu đang được sử dụng hoặc không hợp lệ.").build();
        if (ex instanceof BindException)
            return GraphqlErrorBuilder.newError(env).errorType(ErrorType.BAD_REQUEST)
                    .message("Dữ liệu đầu vào không hợp lệ.").build();
        return null;
    }
}
