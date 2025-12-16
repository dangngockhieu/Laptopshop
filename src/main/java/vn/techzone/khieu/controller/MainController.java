package vn.techzone.khieu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import vn.techzone.khieu.utils.annotation.ApiMessage;

@RestController
@Tag(name = "0. Khác (Misc)", description = "Các API chung kiểm tra trạng thái hệ thống")
public class MainController {
    @GetMapping("/")
    @ApiMessage("Check application home endpoint")
    public String home() {
        return "Welcome to the home page!";
    }

}
