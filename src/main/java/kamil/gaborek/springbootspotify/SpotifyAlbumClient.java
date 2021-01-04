package kamil.gaborek.springbootspotify;

import kamil.gaborek.springbootspotify.entity.Track;
import kamil.gaborek.springbootspotify.model.Item;
import kamil.gaborek.springbootspotify.model.SpotifyAlbum;
import kamil.gaborek.springbootspotify.model.dto.SpotifyAlbumDto;
import kamil.gaborek.springbootspotify.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SpotifyAlbumClient {

    private TrackRepository trackRepository;

    @Autowired
    public SpotifyAlbumClient(TrackRepository trackRepository){
        this.trackRepository = trackRepository;
    }

    @GetMapping("/album/{authorName}")
    public List<SpotifyAlbumDto> getAlbumsByAuthor(OAuth2Authentication details, @PathVariable String authorName) {


        String jwt = ((OAuth2AuthenticationDetails) details.getDetails()).getTokenValue();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<SpotifyAlbum> exchange = restTemplate.exchange("https://api.spotify.com/v1/search?q="+authorName+"&type=track&market=US&limit=10&offset=5",
                HttpMethod.GET,
                httpEntity,
                SpotifyAlbum.class);

        List<SpotifyAlbumDto> tracksList = exchange.getBody().getTracks().getItems()
                .stream()
                .map(item -> new SpotifyAlbumDto(item.getName(), item.getAlbum().getImages().get(0).getUrl()))
                .collect(Collectors.toList());

        return tracksList;
    }

    @PostMapping("/add-tracks")
    public void addTracks(@RequestBody Track track){
        trackRepository.save(track);
    }

}

