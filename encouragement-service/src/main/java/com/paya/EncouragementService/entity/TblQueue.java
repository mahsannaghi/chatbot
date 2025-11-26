package com.paya.EncouragementService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "tbl_queue")
public class TblQueue {


    @Id
    @Column(name = "queue_id")
    private String queueId;

    @PrePersist
    private void id() {
        if (queueId == null) {
            this.queueId = UUID.randomUUID().toString();
        }
    }

    private String requestQueueName;

    private String responseQueueName;



    public String getRequestQueueName() {
        return requestQueueName;
    }

    public void setRequestQueueName(String requestQueueName) {
        this.requestQueueName = requestQueueName;
    }

    public String getResponseQueueName() {
        return responseQueueName;
    }

    public void setResponseQueueName(String responseQueueName) {
        this.responseQueueName = responseQueueName;
    }
}
