package ttit.com.shuvo.docdiary.appt_schedule.health_rank.retrofit_process;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.HealthProgressAdder;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.HealthProgressResponse;

public interface HealthProgressService {
    @POST("health_progress/InsertPatProgress")
    Call<HealthProgressResponse> addHealthProgress(@Body HealthProgressAdder healthProgressAdder);
}
