package de.maxhenkel.modupdate.updateserver;

import java.util.List;

public record ModUpdateResponse(List<ApiErrorDetail> err) {
    public record ApiErrorDetail(String message) {
    }
}
