package org.dominokit.domino.ui.utils;

public class DominoI18n {

  private static DominoI18nProvider provider;
  private static DominoI18nProvider fallbackProvider;

  public static DominoI18nProvider getInstance() {
    if (provider == null && fallbackProvider != null) {
      return fallbackProvider;
    }
    return provider;
  }

  public static void setProvider(DominoI18nProvider provider) {
    DominoI18n.provider = provider;
  }

  public static void setFallbackProvider(DominoI18nProvider provider) {
    DominoI18n.fallbackProvider = provider;
  }
}
