
\version "2.16.2"
% automatically converted by musicxml2ly from musicXML.xml

\header {
    copyright = "All Rights Reserved"
    encodingdate = "2011-10-26"
    title = "Bouree, Suite for Lute in E-minor. 5th movement."
    source = "http://wikifonia.org/node/13271/revisions/23856/view"
    composer = "J.S. Bach"
    encodingsoftware = "MuseScore 1.1"
    }

#(set-global-staff-size 20.0762645669)
\paper {
    paper-width = 21.0\cm
    paper-height = 29.7\cm
    top-margin = 1.0\cm
    bottom-margin = 2.0\cm
    left-margin = 1.0\cm
    right-margin = 1.0\cm
    }
\layout {
    \context { \Score
        autoBeaming = ##f
        }
    }
PartPOneVoiceOne =  \relative e' {
    \clef "treble" \key g \major \numericTimeSignature\time 4/4 \partial
    4 e8 [ fis8 ] | % 1
    g4 fis8 [ e8 ] dis4 e8 [ fis8 ] | % 2
    b,4 cis8 [ dis8 ] e4 d8 [ c8 ] | % 3
    b4 a8 [ g8 ] fis4 g8 [ a8 ] | % 4
    b8 [ a8 g8 fis8 ] e4 e'8 [ fis8 ] \break | % 5
    g4 fis8 [ e8 ] dis4 e8 [ fis8 ] | % 6
    b,4 cis8 [ dis8 ] e4 d8 [ c8 ] | % 7
    b4 a8 [ g8 ] fis4. g8 | % 8
    g2. b8 [ g8 ] \break | % 9
    d'4 a8 [ c8 ] b4 g'8 [ d8 ] | \barNumberCheck #10
    e4 b8 [ d8 ] c4 b8 [ a8 ] | % 11
    gis4 a8 [ b8 ] c4 b8 [ a8 ] | % 12
    a2. d8 [ a8 ] \break | % 13
    b4 fis'8 [ d8 ] e4 b8 [ d8 ] | % 14
    c4 a'8 [ e8 ] fis4 cis8 [ e8 ] | % 15
    d4 cis8 [ b8 ] bes4. b8 | % 16
    b2. b'8 [ fis8 ] \break | % 17
    gis4 fis8 [ e8 ] a4 e8 [ g8 ] | % 18
    fis4 e8 [ d8 ] g4 d8 [ f8 ] | % 19
    e4 a8 [ e8 ] fis4 cis8 [ e8 ] | \barNumberCheck #20
    dis4 b2 e8 [ b8 ] \break | % 21
    c4 d8 [ a8 ] b4 c8 [ g8 ] | % 22
    a4 b8 [ fis8 ] g4 fis8 [ e8 ] | % 23
    dis4 e8 [ fis8 ] g4 fis8 [ e8 ] | % 24
    e2. r4 \bar "|."
    }


% The score definition
\score {
    <<
        \new Staff <<
            \set Staff.instrumentName = "C"
            \context Staff << 
                \context Voice = "PartPOneVoiceOne" { \PartPOneVoiceOne }
                >>
            >>
        
        >>
    \layout {}
    % To create MIDI output, uncomment the following line:
    %  \midi {}
    }

