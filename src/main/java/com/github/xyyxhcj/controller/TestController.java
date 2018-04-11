package com.github.xyyxhcj.controller;

import com.github.xyyxhcj.utils.JsonUtils;
import com.github.xyyxhcj.utils.VerifyCodeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * @author xyyxhcj@qq.com
 * @since 2018-04-10
 */
@RestController
@RequestMapping("test")
public class TestController {
    @GetMapping("index")
    public String index() throws IOException {
        File dir = new File("F:/verifies");
        int w = 200, h = 80;
        String verifyCode = VerifyCodeUtils.generateVerifyCode(5);
        File file = new File(dir, verifyCode + ".jpg");
        VerifyCodeUtils.outputImage(w, h, file, verifyCode);
        return JsonUtils.objectToJson(verifyCode);
    }
}
