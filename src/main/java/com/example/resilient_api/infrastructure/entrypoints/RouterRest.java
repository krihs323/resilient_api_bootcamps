package com.example.resilient_api.infrastructure.entrypoints;

import com.example.resilient_api.infrastructure.entrypoints.handler.BootcampHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;


@Configuration
public class RouterRest {


    @Bean
    public RouterFunction<ServerResponse> routerFunction(BootcampHandlerImpl bootcampHandler) {
        return route()
                .POST("/bootcamp",
                        bootcampHandler::createBootcamp,
                        ops -> ops.beanClass(BootcampHandlerImpl.class).beanMethod("createBootcamp"))

                .GET("/bootcamp",
                        bootcampHandler::listBootcamp,
                        ops -> ops.beanClass(BootcampHandlerImpl.class).beanMethod("listBootcamp")
                )
                .build();
    }
}