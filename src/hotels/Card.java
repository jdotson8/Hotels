/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.time.LocalDate;

/**
 *
 * @author Administrator
 */
public class Card {
    private String cardNum;
    private LocalDate expDate;
    
    public Card(String cardNum, LocalDate expDate) {
        this.cardNum = cardNum;
        this.expDate = expDate;
    }
    
    public String getCardNum() {
        return cardNum;
    }
    
    public LocalDate getExpDate() {
        return expDate;
    }
    
    @Override
    public String toString() {
        char[] str = cardNum.toCharArray();
        for (int i = str.length - 5; i >= 0; i--) {
            str[i] = '•';
        }
        return String.valueOf(str);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            Card other = (Card) o;
            return cardNum.equals(other.cardNum);
        } else {
            return false;
        }
    }
}
