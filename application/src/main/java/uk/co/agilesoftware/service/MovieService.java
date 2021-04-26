package uk.co.agilesoftware.service;

import lombok.RequiredArgsConstructor;
import uk.co.agilesoftware.upstream.LexiconMovieConnector;

@RequiredArgsConstructor
public class MovieService {
    private final LexiconMovieConnector cineworldConnector;
    private final LexiconMovieConnector filmworldConnector;

}
