package org.example.pojo;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Customer {
    @NonNull
    final Integer id;
    @NonNull
    public String name;
    @NonNull
    public String lastName;
    List<Integer> accounts;
}
