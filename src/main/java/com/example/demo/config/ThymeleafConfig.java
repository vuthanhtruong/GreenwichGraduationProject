package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class ThymeleafConfig {

    @Bean
    public SpringResourceTemplateResolver templateResolver1() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/home/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(1);
        resolver.setCheckExistence(true);
        return resolver;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver2() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/DeputyStaffs/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(2);
        resolver.setCheckExistence(true);
        return resolver;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver3() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/classes/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(3);
        resolver.setCheckExistence(true);
        return resolver;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver4() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Syllabus/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(4);
        resolver.setCheckExistence(true);
        return resolver;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver5() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Student/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(5);
        resolver.setCheckExistence(true);
        return resolver;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver6() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/News/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(6);
        resolver.setCheckExistence(true);
        return resolver;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver7() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Room/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(7);
        resolver.setCheckExistence(true);
        return resolver;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver8() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Class/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(8);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver9() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Staff/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(9);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver10() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Login/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(10);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver11() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Classroom/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(11);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver12() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/AccountBalance/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(12);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver13() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Subject/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(13);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver14() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/StudentRequiredSpecializedSubjects/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(14);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver15() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Admin/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(15);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver16() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Campus/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(16);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver17() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/StudentRequiredSubjects/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(17);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver18() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/AcademicTranscript/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(18);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver19() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Lecturer/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(19);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver20() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Scholarship/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(20);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver21() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/ListEmailTemplate/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(21);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver22() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/TuitionFeesByCampus/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(22);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver23() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Major/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(23);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver24() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Curriculums/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(24);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver25() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Specializations/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(25);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver26() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/StudentRequiredSpecializedSubjects/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(26);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver27() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/Roadmap/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(27);
        resolver.setCheckExistence(true);
        return resolver;
    }
    @Bean
    public SpringResourceTemplateResolver templateResolver28() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/MajorLecturer/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(28);
        resolver.setCheckExistence(true);
        return resolver;
    }

}
