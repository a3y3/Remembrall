package com.example.soham.remembrall;

/**
 * Created by Soham on 23-Mar-17.
 */

public class NoteHolder {
    int _id;
    String _title;
    String _note;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String get_note() {
        return _note;
    }

    public void set_note(String _note) {
        this._note = _note;
    }
    public NoteHolder(int _id, String _title,String _note){
        this._id = _id;
        this._title = _title;
        this._note = _note;
    }


}
