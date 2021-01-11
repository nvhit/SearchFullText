package com.demo.searchfulltext.util.text;

import com.demo.searchfulltext.util.StringPool;

import java.io.*;
import java.util.Iterator;

/**
 * Abstraction for reading the context of a text file into a String. Implements the
 * Iterator pattern to read the content of a text file a single line at a time.
 */
public class TextFileReader implements Iterable<String> {

  /**
   * The full name of the text file from which the content will be read.
   */
  private final String fileName;


  /**
   * Create a new instance of the text file reader to get the content of the
   * file represented by the specified {@code fileName}.
   *
   * @param fileName the full path and file name of the file to be
   *                 read. Should not be {@code null}.
   * @throws AssertionError if the {@code fileName} is {@code null} and
   *                        the {@literal -ea} flag on the vm has boeen set.
   */
  public TextFileReader(final String fileName) {
    assert fileName != null;
    this.fileName = fileName;
  }

  /**
   * Create a new iterator to iterate through the contents of the file.
   *
   * @return an iterator to return the content of the file
   * line by line as a String. Never {@code null}.
   */
  @Override
  public Iterator<String> iterator() {
    return new TextFileIterator();
  }

  /**
   * An implementation of the Iterator pattern that iterates through the
   * contents of a text file returning each line in the file in turn.
   */
  private final class TextFileIterator implements Iterator<String> {

    /**
     * The input stream used to read the content of the text file.
     */
    final BufferedReader in;

    /**
     * The next line of text to be returned on the next call to {@link #next()}.
     */
    String nextline;

    /**
     * Create a new instance of the iterator class to read the content from
     * the {@link TextFileReader#fileName specified text file}.
     * This constructor is responsible for opening a stream to read the content
     * as well as reading the first line of text from the file.
     *
     * @throws IllegalArgumentException if an error occurs opening the input stream or
     *                                  reading the first line of text from the file.
     */
    public TextFileIterator() {
      try {
        in = new BufferedReader(new FileReader(fileName));
        nextline = in.readLine(); // We peek ahead like this for the benefit of hasNext( ).
      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
    }

    /**
     * Check if there is another line of text to be returned from this
     * iterator.
     *
     * @return {@code true} if there is another line to
     * be returned, {@code false} otherwise.
     */
    @Override
    public boolean hasNext() {
      return nextline != null;
    }

    /**
     * Return the next line of text from the iterator. This method will
     * first read the line of text following the next line of text to be
     * returned. If no line follows the next line to be returned, this
     * method will close the input stream being used to read text.
     *
     * @throws IllegalArgumentException if an error occurs trying to read from the
     *                                  file or attempting to close the file after
     *                                  all lines of text have been read.
     */
    public String next() {
      try {
        String result = nextline;
        // If we haven't reached EOF yet
        if (nextline != null) {
          nextline = in.readLine();   // Read another line
          if (nextline == null) {
            in.close();     // And close on EOF
          }
        }
        // Return the line we read last time through.
        return result;

      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
    }

    /**
     * This method is not supported since the file is read-only we
     * do not allow lines to be removed from the file.
     * always.
     */
    public void remove() {
      throw new UnsupportedOperationException();
    }

  }

  /**
   * @param files
   */
  public void reading(String[] files) {
    for (String fileName : files) {
      for (final String line : new TextFileReader(fileName)) {
        System.out.println(line);
      }
    }
  }

  static public String[] readingInFolder(String _folder, String _endWith) {
    File file = new File(_folder);
    FilenameFilter filter = new FilenameFilter() {
      @Override
      public boolean accept(File file, String name) {
        return name.toLowerCase().endsWith(_endWith);
      }
    };
    return file.list(filter);
  }


  /**
   * A simple demonstration of how the class can be used to read the content of a text file
   * and print the content to the screen.
   *
   * @param args takes an optional single argument that is the full path to the
   *             text file to be read. If no argument is supplied then this demo
   *             will attempt to read the context of this java file.
   */
  public static void main(String[] args) {
    final String fileName = (args.length > 0) ? args[0] : "D:\\Source\\SearchFullText\\1.txt";

//    for (final String line : new TextFileReader(fileName)) {
//      System.out.println(line);
//    }

  }

}
