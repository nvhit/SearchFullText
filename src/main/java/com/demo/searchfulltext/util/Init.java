package com.demo.searchfulltext.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Init {
  final int NUM_DEFAULT = 0;

  int indexBeginHeader() default NUM_DEFAULT;

  int indexEndHeader() default NUM_DEFAULT;

  int headerIndexColumn() default NUM_DEFAULT;
}
