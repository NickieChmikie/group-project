package use_case.generate;

import API_calls.GetToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import API_calls.GetPlaylist;

public class CreatePlaylistHelper {

    private static final String token = GetToken.getToken();
    static int popularity;
    static float energy;
    static float speechiness;
    static float valence;
    static float danceability;
    static int num_of_tracks;
    static String genre;
    public List<Object> generatePlaylists(String genre, int popularity, float danceability, float valence,
                                          float speechiness, float energy) throws IOException {
        ArrayList<Object> api_call = GetPlaylist.getPlaylistCall(popularity, energy, speechiness, valence,
                danceability, num_of_tracks, genre);
        OkHttpClient client = (OkHttpClient) api_call.get(0);
        Request request = (Request) api_call.get(1); //info from api_call method

        Response tracks = client.newCall(request).execute(); // stores the tracks in a Response Object (standard practice, not exactly sure)
        //System.out.println(tracks.body().string()); //note, whenever the .string() thing is run, it closes tracks, and it becomes unreadable from then on

        List<Object> songItems = new ArrayList<>();
        List<String> trackNames = getTrackNames(tracks);
        songItems.add(trackNames);
        List<List<String>> trackArtists = getTrackArtists(tracks);
        songItems.add(trackArtists);
        List<String> trackLinks = getTrackLinks(tracks);
        songItems.add(trackLinks);
        List<String> trackDates = getTrackDates(tracks);
        songItems.add(trackDates);
        List<Integer> trackPopularities = getTrackPopularities(tracks);
        songItems.add(trackPopularities);
        //System.out.println(trackNames);

        try {
            System.out.println("Response Code: " + tracks.code());
            ResponseBody responseBody = tracks.body();
            if (!tracks.isSuccessful()) {
                String errorBody = responseBody.string();
                System.out.println("Request failed with code: " + tracks.code());
                System.out.println("Error Body: " + errorBody);
                throw new RuntimeException("Check that the genre is input or that the token is refreshed");
            }

            //System.out.println("Response Body: " + String.valueOf(responseBody));
        } finally {
            tracks.close(); //avoids potential errors of closing prematurely (I don't remember exactly, but it could contribute to 400 code)
        } //should be done last! .close, closes tracks and becomes unreadable. Also note .string() does the same thing
        return songItems;
    }
    public static List<List<String>> getTrackArtists(Response response) {
        List<List<String>> trackArtists = new ArrayList<>();

        try {
            // Read the response body content
            String responseBody = Objects.requireNonNull(response.body()).string(); //Intellij autocorrected

            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            // Access the "tracks" array
            JsonNode tracksArray = objectMapper.readTree(responseBody).path("tracks"); //found this JSON reading method online
            for (JsonNode trackNode : tracksArray) {
                JsonNode jsonArtists = trackNode.path("artists");
                ArrayList<String> artistsSong = new ArrayList<>();
                for (JsonNode artistNode : jsonArtists) {
                    String trackArtist = artistNode.path("name").asText();
                    artistsSong.add(trackArtist);
                }
                trackArtists.add(artistsSong); // adds List of Artists of a Song
            }

        } catch (IOException e) {
            throw new RuntimeException(e); // necessary for the .string() to work
        }

        return trackArtists;
    }

    public static List<String> getTrackNames(Response response) {
        List<String> trackNames = new ArrayList<>();

        try {
            // Read the response body content
            String responseBody = Objects.requireNonNull(response.body()).string(); //Intellij autocorrected

            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            // Access the "tracks" array
            JsonNode tracksArray = objectMapper.readTree(responseBody).path("tracks"); //found this JSON reading method online

            // retrieves the id field and stores it
            for (JsonNode trackNode : tracksArray) {
                String trackName = trackNode.path("name").asText(); //converts JSON to String
                trackNames.add(trackName); // adds to trackIds array
                System.out.println("Track Name: " + trackName); //prints Track ID (with these settings should be Kashmir and Steady as She Goes)
            }

        } catch (IOException e) {
            throw new RuntimeException(e); // necessary for the .string() to work
        }

        return trackNames;
    }
    public static List<String> getTracks(Response response) {
        List<String> trackIds = new ArrayList<>();

        try {
            // Read the response body content
            String responseBody = Objects.requireNonNull(response.body()).string(); //Intellij autocorrected

            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            // Access the "tracks" array
            JsonNode tracksArray = objectMapper.readTree(responseBody).path("tracks"); //found this JSON reading method online

            // retrieves the id field and stores it
            for (JsonNode trackNode : tracksArray) {
                String trackId = trackNode.path("id").asText(); //converts JSON to String
                trackIds.add(trackId); // adds to trackIds array
                System.out.println("Track ID: " + trackId); //prints Track ID (with these settings should be Kashmir and Steady as She Goes)
            }

        } catch (IOException e) {
            throw new RuntimeException(e); // necessary for the .string() to work
        }

        return trackIds;
    }
    public static List<Integer> getTrackPopularities(Response response) {
        List<Integer> trackPops = new ArrayList<>();

        try {
            // Read the response body content
            String responseBody = Objects.requireNonNull(response.body()).string(); //Intellij autocorrected

            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            // Access the "tracks" array
            JsonNode tracksArray = objectMapper.readTree(responseBody).path("tracks"); //found this JSON reading method online

            // retrieves the id field and stores it
            for (JsonNode trackNode : tracksArray) {
                Integer trackPop = trackNode.path("popularity").asInt(); //converts JSON to String
                trackPops.add(trackPop); // adds to trackIds array
            }

        } catch (IOException e) {
            throw new RuntimeException(e); // necessary for the .string() to work
        }

        return trackPops;
    }

    public static List<String> getTrackDates(Response response) {
        List<String> trackDates = new ArrayList<>();

        try {
            // Read the response body content
            String responseBody = Objects.requireNonNull(response.body()).string(); //Intellij autocorrected

            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            // Access the "tracks" array
            JsonNode tracksArray = objectMapper.readTree(responseBody).path("tracks"); //found this JSON reading method online

            // retrieves the id field and stores it
            for (JsonNode trackNode : tracksArray) {
                String trackDate = trackNode.path("album").path("release_date").asText(); //converts JSON to String
                trackDates.add(trackDate); // adds to trackIds array
            }

        } catch (IOException e) {
            throw new RuntimeException(e); // necessary for the .string() to work
        }

        return trackDates;
    }



    public static List<String> getTrackLinks(Response response){
        List<String> trackLinks = new ArrayList<>();
        List<String> local_tracks = getTracks(response);

        for (String trackId : local_tracks) {
            String trackLink = "https://open.spotify.com/track/" + trackId;
            trackLinks.add(trackLink);
            System.out.println("Link: " + trackLink); //prints Track ID (with these settings should be Kashmir and Steady as She Goes)
        }

        return trackLinks;
    }
}