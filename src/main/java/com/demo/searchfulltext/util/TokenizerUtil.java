package com.demo.searchfulltext.util;

import java.text.Normalizer;
import java.util.StringTokenizer;

public class TokenizerUtil extends StringTokenizer {

  public TokenizerUtil(String str, String delim, boolean returnDelims) {
    super(str, delim, returnDelims);
    nomalizerUNICODE(str);

  }

  public TokenizerUtil(String str, String delim) {
    super(str, delim);
  }

  public TokenizerUtil(String str) {
    super(str);
  }

  //  public Normalizer(String str, String delim, boolean returnDelims, boolean filterMark) {
//    str = Normalizer.normalize(str, Normalizer.Form.NFD);
//    super(str, delim, returnDelims);
//  }
  static public String nomalizerUNICODE(String str) {
    return Normalizer.normalize(str, Normalizer.Form.NFD);

  }

}
