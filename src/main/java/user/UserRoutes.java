package user;

import com.fasterxml.jackson.databind.ObjectMapper;
import http.HttpResponse;
import http.HttpStatus;
import router.Router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserRoutes {

    public static List<User> userList = new ArrayList<>(Arrays.asList(
            new User(1, "조민수"),
            new User(2, "황제원"),
            new User(3, "서은건")
    ));

    static final ObjectMapper objectMapper = new ObjectMapper();

    public static void register(Router router) {
        router.get("/users", request -> {
            try {
                String json = objectMapper.writeValueAsString(userList);

                return HttpResponse.text(
                        HttpStatus.OK,
                        json
                );
            } catch (Exception e) {
                return HttpResponse.text(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "JSON 변환 실패"
                );
            }
        });

        router.post("/users", request -> {

            try {
                CreateUserRequest createUserRequest = objectMapper.readValue(
                        request.body().text(),
                        CreateUserRequest.class
                );

                userList.add(new User(userList.size() + 1, createUserRequest.name()));

                return HttpResponse.text(
                        HttpStatus.CREATED,
                        "추가되었습니다."
                );

            } catch (Exception e) {
                return HttpResponse.text(
                        HttpStatus.BAD_REQUEST,
                        "잘못된 JSON입니다."
                );
            }
        });
    }
}
