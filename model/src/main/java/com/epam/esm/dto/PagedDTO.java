package com.epam.esm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.PagedModel;
import java.util.ArrayList;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedDTO <T>{

    private Collection<T> page = new ArrayList<>();

    private PagedModel.PageMetadata pageMetadata ;

    public boolean isEmpty(){
        return page.isEmpty();
    }
}
