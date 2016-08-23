package com.suda.voice;

/**
 * Edited by YangJiali on 2016/8/17 0017.
 */
public class Book {
    //基本信息，根据书名可获取
    private String bid;
    private String name;
    private String author;
    private String publisher;
    private String isbn;
    //详细信息，根据bid号可获取
    private String callNo;
    private String[] holdPlaces;
    private String[] holdStatus;

    public Book(){

    }
    public Book(String bId, String name, String bAuthor, String bPublisher, String isbn,
                String callNo, String[] holdPlaces, String[] holdStatus) {
        this.bid = bId;
        this.name = name;
        this.author = bAuthor;
        this.publisher = bPublisher;
        this.isbn = isbn;
        this.callNo = callNo;
        this.holdPlaces = holdPlaces;
        this.holdStatus = holdStatus;
    }


    public String getbId() {

        return bid;
    }

    public void setbId(String bId) {
        this.bid = bId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getbAuthor() {
        return author;
    }

    public void setbAuthor(String bAuthor) {
        this.author = bAuthor;
    }

    public String getbPublisher() {
        return publisher;
    }

    public void setbPublisher(String bPublisher) {
        this.publisher = bPublisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCallNo() {
        return callNo;
    }

    public void setCallNo(String callNo) {
        this.callNo = callNo;
    }

    public String[] getHoldPlaces() {
        return holdPlaces;
    }

    public void setHoldPlaces(String[] holdPlaces) {
        this.holdPlaces = holdPlaces;
    }

    public String[] getHoldStatus() {
        return holdStatus;
    }

    public void setHoldStatus(String[] holdStatus) {
        this.holdStatus = holdStatus;
    }
}
