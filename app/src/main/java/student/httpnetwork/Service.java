package student.httpnetwork;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service {
     private Retrofit retrofit;
     private Gson gson;

     public Service() {
         gson = new GsonBuilder().create();
         retrofit = new   Retrofit.Builder()
         .baseUrl(NetConstants.BASE_URL)
         .addConverterFactory(GsonConverterFactory.create(gson))
         .build();
     }

     public NetActions get(){
         return  this.retrofit.create(NetActions.class);
     }

}
