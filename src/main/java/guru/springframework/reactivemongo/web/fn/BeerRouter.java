package guru.springframework.reactivemongo.web.fn;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class BeerRouter {
    public static final String BEER_PATH = "/api/v3/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    private final BeerHandler handler;

    @Bean
    public RouterFunction<ServerResponse> beerRouterImpl() {
        return route()
                .GET(BEER_PATH, accept(APPLICATION_JSON), handler::beers)
                .GET(BEER_PATH_ID, accept(APPLICATION_JSON), handler::findById)
                .POST(BEER_PATH, accept(APPLICATION_JSON), handler::createBeer)
                .PUT(BEER_PATH_ID, handler::updateById)
                .PATCH(BEER_PATH_ID, handler::patchById)
                .DELETE(BEER_PATH_ID, handler::deleteById)
                .build();
    }
}
