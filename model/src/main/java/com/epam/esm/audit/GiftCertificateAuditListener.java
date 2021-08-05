package com.epam.esm.audit;

import com.epam.esm.entity.GiftCertificate;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

public class GiftCertificateAuditListener {

    @PrePersist
    public void setDateTimeAndDeletedBeforeCreating(GiftCertificate giftCertificate){
        giftCertificate.setCreateDate(LocalDateTime.now());
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        giftCertificate.setDeleted(false);
    }

    @PreUpdate
    public void setDateTimeBeforeUpdating(GiftCertificate giftCertificate){
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
    }
}