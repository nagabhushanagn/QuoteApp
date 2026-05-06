/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.service.optimizer;

import com.amitron.quoteapp.model.optimizer.ArrayDefinition;
import com.amitron.quoteapp.model.optimizer.Board;

/**
 *
 * @author Ngn
 */
public class ArrayCalculator {

    public static Board toBoard(ArrayDefinition arr) {

        return new Board(
                arr.getArrayWidth(),
                arr.getArrayHeight()
        );
    }
}
