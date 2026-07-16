package com.certifypro.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Builds a safe Pageable from page/limit query params (NFR: pagination on all lists). */
public final class PageUtil {

    private PageUtil() {
    }

    public static Pageable of(int page, int limit) {
        int p = Math.max(page, 0);
        int l = limit < 1 ? 10 : Math.min(limit, 100);
        return PageRequest.of(p, l);
    }

    public static Pageable of(int page, int limit, Sort sort) {
        int p = Math.max(page, 0);
        int l = limit < 1 ? 10 : Math.min(limit, 100);
        return PageRequest.of(p, l, sort);
    }
}
