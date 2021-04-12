package com.gatos_app;


import com.google.gson.Gson;
import okhttp3.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class GatosService {

    public static void verGatos() throws IOException{
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/images/search")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        String elJson = response.body().string();

        //Cortar los corchetes

        elJson = elJson.substring(1,elJson.length());
        elJson=elJson.substring(0,elJson.length()-1);

        //crear un objeto de clase gson

        Gson gson = new Gson();
        Gatos gatos = gson.fromJson(elJson,Gatos.class);

        //redimensionar imagenes
        Image image= null;
        try{
            URL url= new URL(gatos.getUrl());
            image= ImageIO.read(url);

            ImageIcon fondoGato = new ImageIcon(image);
            if (fondoGato.getIconWidth() > 800){
                //redimensionamos
                Image fondo = fondoGato.getImage();
                Image modificada = fondo.getScaledInstance(800,600, Image.SCALE_SMOOTH);
                fondoGato =new ImageIcon(modificada);
            }

            String menu = "Opciones: \n"
                    +"1 ver otra imagen \n"
                    +"2. Favorito \n"
                    +"3. Volver \n";

            String[] botones = {"Ver otra imagen","Favorito","Volver"};

            String id_gato= gatos.getId();
            String opcion=(String) JOptionPane.showInputDialog(null,menu,id_gato,JOptionPane.INFORMATION_MESSAGE,
                    fondoGato,botones,botones[0]);

            int seleccion = -1;

            for(int i=0;i< botones.length;i++){
                if (opcion.equals(botones[i])){
                    seleccion =i;
                }}

            switch (seleccion){
                case 0:
                    verGatos();
                    break;
                case 1:
                    FavoritoGato(gatos);
                    break;
                default:
                    break;
            }


        }catch (IOException e){
            System.out.println(e);
        }
    }

    public static void FavoritoGato(Gatos gato){

       try{
           OkHttpClient client = new OkHttpClient().newBuilder()
                   .build();
           MediaType mediaType = MediaType.parse("application/json");
           RequestBody body = RequestBody.create(mediaType, "{\r\n  \"image_id\": \""+gato.getId()+"\"\r\n}");
           Request request = new Request.Builder()
                   .url("https://api.thecatapi.com/v1/favourites")
                   .method("POST", body)
                   .addHeader("Content-Type", "application/json")
                   .addHeader("x-api-key", gato.getApikey())
                   .build();
           Response response = client.newCall(request).execute();

       }catch (IOException e){
           System.out.println(e);

       }




    }

    public static void verFavoritos(String apikey) throws IOException{
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/favourites")
                .method("GET", null)
                .addHeader("x-api-key", "d6cb32cc-8e43-4c1c-9182-eec7395adc4d")
                .build();
        Response response = client.newCall(request).execute();

        //guardamos el string con la respuesta
        String elJson= response.body().string();

        //creamos el objeto gson
        Gson gson = new Gson();

        GatosFav[] gatosArray= gson.fromJson(elJson,GatosFav[].class);

        if (gatosArray.length> 0){
            int min= 1;
            int max= gatosArray.length;
            int aleatorio = (int) (Math.random() * ((max-min)+1)) +min;
            int indice = aleatorio-1;

            GatosFav gatosFav= gatosArray[indice];

            //redimensionar imagenes
            Image image= null;
            try{
                URL url= new URL(gatosFav.getImage().getUrl());
                image= ImageIO.read(url);

                ImageIcon fondoGato = new ImageIcon(image);
                if (fondoGato.getIconWidth() > 800){
                    //redimensionamos
                    Image fondo = fondoGato.getImage();
                    Image modificada = fondo.getScaledInstance(800,600, Image.SCALE_SMOOTH);
                    fondoGato =new ImageIcon(modificada);
                }

                String menu = "Opciones: \n"
                        +"1 ver otra imagen \n"
                        +"2. Eliminar Favorito \n"
                        +"3. Volver \n";

                String[] botones = {"Ver otra imagen","Eliminar Favorito","Volver"};

                String id_gato= gatosFav.getId();
                String opcion=(String) JOptionPane.showInputDialog(null,menu,id_gato,JOptionPane.INFORMATION_MESSAGE,
                        fondoGato,botones,botones[0]);

                int seleccion = -1;

                for(int i=0;i< botones.length;i++){
                    if (opcion.equals(botones[i])){
                        seleccion =i;
                    }}

                switch (seleccion){
                    case 0:
                        verFavoritos(apikey);
                        break;
                    case 1:
                        borrarFavorito(gatosFav);
                        break;
                    default:
                        break;
                }


            }catch (IOException e){
                System.out.println(e);
            }
        }
    }

    public static void borrarFavorito(GatosFav gatosfav){

        try{

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites/"+gatosfav.getId()+"")
                    .method("DELETE", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", gatosfav.getApikey())
                    .build();
            Response response = client.newCall(request).execute();

        }catch (IOException e){
            System.out.println(e);
        }



    }
}
