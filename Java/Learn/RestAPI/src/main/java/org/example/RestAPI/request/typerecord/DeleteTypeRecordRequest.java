package org.example.RestAPI.request.typerecord;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteTypeRecordRequest {
    private long typeRecord_id;
}
