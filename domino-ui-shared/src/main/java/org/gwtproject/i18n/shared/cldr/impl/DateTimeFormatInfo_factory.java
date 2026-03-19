/*
 * Copyright © 2019 Dominokit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtproject.i18n.shared.cldr.impl;

import org.gwtproject.i18n.shared.cldr.DateTimeFormatInfo;

public class DateTimeFormatInfo_factory {
  public static DateTimeFormatInfo create() {
    return new DateTimeFormatInfo() {
      @Override
      public String[] ampms() {
        return new String[] {"AM", "PM"};
      }

      @Override
      public int firstDayOfTheWeek() {
        return 0;
      }

      @Override
      public String[] monthsFull() {
        return new String[] {
          "January",
          "February",
          "March",
          "April",
          "May",
          "June",
          "July",
          "August",
          "September",
          "October",
          "November",
          "December"
        };
      }

      @Override
      public String[] monthsShort() {
        return new String[] {
          "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
      }

      @Override
      public String[] weekdaysFull() {
        return new String[] {
          "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        };
      }

      @Override
      public String[] weekdaysShort() {
        return new String[] {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
      }

      @Override
      public int weekendStart() {
        return 6;
      }

      @Override
      public int weekendEnd() {
        return 0;
      }

      @Override
      public String timeFormatFull() {
        return "HH:mm:ss";
      }

      @Override
      public String timeFormatLong() {
        return "HH:mm:ss";
      }

      @Override
      public String timeFormatMedium() {
        return "HH:mm:ss";
      }

      @Override
      public String timeFormatShort() {
        return "HH:mm";
      }

      @Override
      public String formatHour24MinuteSecond() {
        return "HH:mm:ss";
      }

      @Override
      public String formatHour24Minute() {
        return "HH:mm";
      }

      @Override
      public String formatHour12MinuteSecond() {
        return "hh:mm:ss a";
      }

      @Override
      public String formatHour12Minute() {
        return "hh:mm a";
      }

      @Override
      public String dateFormatFull() {
        return "yyyy-MM-dd";
      }

      @Override
      public String dateFormatLong() {
        return "yyyy-MM-dd";
      }

      @Override
      public String dateFormatMedium() {
        return "yyyy-MM-dd";
      }

      @Override
      public String dateFormatShort() {
        return "yyyy-MM-dd";
      }
    };
  }
}
