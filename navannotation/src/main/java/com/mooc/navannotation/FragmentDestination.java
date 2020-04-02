package com.mooc.navannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface FragmentDestination {
    String pageUrl();

    boolean isNeedLogin() default false;

    boolean asStarter() default false;
}
