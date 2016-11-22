package com.suda.utils.knn;

/**
 * Created by YangJiali on 2016/11/20 0020.
 * 借阅记录
 */
public class Borrow_record {
    String reader_id;
    String book_id;

    public Borrow_record(String reader_id, String book_id) {
        this.reader_id = reader_id;
        this.book_id = book_id;
    }

    public String getReader_id() {
        return reader_id;
    }

    public void setReader_id(String reader_id) {
        this.reader_id = reader_id;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }
}
