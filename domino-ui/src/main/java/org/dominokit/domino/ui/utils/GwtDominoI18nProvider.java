package org.dominokit.domino.ui.utils;

import org.gwtproject.i18n.client.NumberFormat;
import org.gwtproject.i18n.shared.DateTimeFormat;
import org.gwtproject.i18n.shared.cldr.DateTimeFormatInfo;
import org.gwtproject.i18n.shared.cldr.impl.DateTimeFormatInfo_factory;

public class GwtDominoI18nProvider implements DominoI18nProvider {

  // Register this as the fallback for GWT builds statically.
  static {
    DominoI18n.setFallbackProvider(new GwtDominoI18nProvider());
  }

  // Force initialization of the static block
  public static void init() {
    // no-op, just loads the class
  }

  private DateTimeFormatInfo dtfi = DateTimeFormatInfo_factory.create();

  @Override
  public String formatNumber(Number number, String pattern) {
    if (pattern != null && !pattern.isEmpty()) {
      return NumberFormat.getFormat(pattern).format(number);
    }
    return NumberFormat.getDecimalFormat().format(number);
  }

  @Override
  public Number parseNumber(String text, String pattern) {
    if (pattern != null && !pattern.isEmpty()) {
      return NumberFormat.getFormat(pattern).parse(text);
    }
    return NumberFormat.getDecimalFormat().parse(text);
  }

  @Override
  public String formatDate(java.util.Date date, String pattern) {
    if (pattern != null && !pattern.isEmpty()) {
      return DateTimeFormat.getFormat(pattern).format(date);
    }
    return DateTimeFormat.getFormat("yyyy-MM-dd").format(date);
  }

  @Override
  public java.util.Date parseDate(String text, String pattern) {
    if (pattern != null && !pattern.isEmpty()) {
      return DateTimeFormat.getFormat(pattern).parse(text);
    }
    return DateTimeFormat.getFormat("yyyy-MM-dd").parse(text);
  }

  @Override
  public String[] getMonthsNames() {
    return dtfi.monthsFull();
  }

  @Override
  public String[] getMonthsShortNames() {
    return dtfi.monthsShort();
  }

  @Override
  public String[] getWeekdaysNames() {
    return dtfi.weekdaysFull();
  }

  @Override
  public String[] getWeekdaysShortNames() {
    return dtfi.weekdaysShort();
  }
}
