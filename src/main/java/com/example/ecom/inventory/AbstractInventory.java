package com.example.ecom.inventory;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractInventory<r> {
    @Autowired
    protected r repository;
}
