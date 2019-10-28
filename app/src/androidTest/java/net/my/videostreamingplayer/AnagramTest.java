package net.my.videostreamingplayer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AnagramTest {
    private Anagram.AnagramCheck anagramCheck = new Anagram.AnagramCheck();

    @Test
    public void main() {
        anagramCheck.checkAnagram("table","bleta");
    }
}