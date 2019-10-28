package net.my.videostreamingplayer;

import java.util.Arrays;

public class Anagram {

  public static void main(String[] args) {

    //two strings
    String str1 = "bleat";
    String str2 = "table";

    AnagramCheck anagramCheck = new AnagramCheck();
    anagramCheck.checkAnagram(str1, str2);
  }

  public static class AnagramCheck {

    public AnagramCheck() {
    }

    public void checkAnagram(String str1, String str2) {
      //convert character arrays
      char[] charArray1 = str1.toCharArray();
      char[] charArray2 = str2.toCharArray();

      Arrays.sort(charArray1);
      Arrays.sort(charArray2);

      boolean blnResult = Arrays.equals(charArray1, charArray2);

      if (blnResult) {
        System.out.println("Strings are Anagram");
      } else {
        System.out.println("Strings are not Anagram");
      }

    }
  }
}