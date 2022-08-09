package com.example.beccaccino.room;

import androidx.room.TypeConverter;
import com.example.beccaccino.model.entities.ItalianCard;


public class BeccaccinoTypeConverter {

    @TypeConverter
    public String suitToRoom(ItalianCard.Suit suit) {
        return suit.toString();
    }

    @TypeConverter
    public ItalianCard.Suit suitFromRoom(String string) {
        return ItalianCard.Suit.valueOf(string);
    }

    @TypeConverter
    public String valueToRoom(ItalianCard.Value value) {
        return value.toString();
    }

    @TypeConverter
    public ItalianCard.Value valueFromRoom(String string) {
        return ItalianCard.Value.valueOf(string);
    }
}
