package com.demo.searchfulltext.demo;

import com.demo.searchfulltext.util.StringPool;
import com.demo.searchfulltext.util.StringUtils;
import com.demo.searchfulltext.util.TokenizerUtil;
import com.demo.searchfulltext.util.text.TextFileReader;

import java.util.StringTokenizer;

public class Demo {
  public static void main(String[] args) throws Exception {
    String folder = "D:\\Source\\SearchFullText";

    String[] files = TextFileReader.readingInFolder(folder, StringPool.END_WITH_TXT);
    for (String f : files) {
      for (String line : new TextFileReader(folder + StringPool.BACK_SLASH + f)) {
        System.out.println(line);
        // line = StringUtils.replaceVietnameseCharacter(line);
        line = line.replaceAll("\\p{Punct}", "");
        line = StringUtils.removeAccent(line);
        TokenizerUtil st = new TokenizerUtil(line, " ", false);

        while (st.hasMoreTokens()) {
          System.out.println(st.nextToken());

        }

      }
    }

  }
}
