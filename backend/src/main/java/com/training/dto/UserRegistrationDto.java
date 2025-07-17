package com.training.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class UserRegistrationDto {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^(男|女)$", message = "性别只能是男或女")
    private String gender;

    @NotBlank(message = "身份证号码不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", 
             message = "身份证号码格式不正确")
    private String idCard;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "工作单位不能为空")
    private String workUnit;

    @NotBlank(message = "培训类型不能为空")
    private String trainingType;

    @NotBlank(message = "作业类别不能为空")
    private String jobCategory;

    @NotNull(message = "缴费额度不能为空")
    @DecimalMin(value = "0.0", message = "缴费额度不能为负数")
    private Double paymentAmount;
} 