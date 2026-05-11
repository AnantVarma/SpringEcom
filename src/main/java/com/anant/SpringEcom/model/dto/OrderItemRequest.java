package com.anant.SpringEcom.model.dto;

public record OrderItemRequest(
        int productId,
        int quantity
) {}