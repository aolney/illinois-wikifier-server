# illinois-wikifier-server

This repo provides a simple REST API for the 2013 version of the [Cognitive Computation Group Wikifier](https://cogcomp.seas.upenn.edu/page/demo_view/Wikifier).

## Installation

[Download](https://cogcomp.seas.upenn.edu/page/download_view/Wikifier) and unzip version 2 of the wikifier to get started.

Then unzip this repo at the base directory (where the wikifier readme is located). It should put the two corresponding files in the right location. If something goes wrong, the correct location is:

`Wikifier2013/src/edu/illinois/cs/cogcomp/wikifier/apps/demo`

Once the files are installed, rebuild the combined jar:

`mvn clean compile assembly:single`

The output jar will be in `target`.

## Server

The server is designed to work with the existing wikifier configuration files. An example call is:

`java -Xmx10G -cp target/wikifier-3.0-jar-with-dependencies.jar edu.illinois.cs.cogcomp.wikifier.apps.demo.OlneyServer configs/STAND_ALONE_NO_INFERENCE.xml`

It doesn't currently work with Gurobi 9.1.1, but it might if changes were made to the [LBJ library](https://mvnrepository.com/artifact/edu.illinois.cs.cogcomp/edu.illinois.cs.cogcomp.lbj/2.8.2).

## Client

[Postman](https://www.postman.com/) files show how to use the API. A GET request is `text=Some text here` and a POST request is `{text:"Some text here"}`. CORS is hopefully working but this hasn't been fully tested.

Here is some example output:

```json
{
    "inputText": "Emotional stress can either increase or decrease TRH and TSH secretion, depending upon circumstances.",
    "entities": [
        {
            "surfaceForm": "Emotional stress",
            "start": 0,
            "stop": 16,
            "candidates": [
                {
                    "score": 1.0,
                    "wikiTitle": "Stress_(biological)",
                    "wikiId": 146072,
                    "attributes": ""
                }
            ]
        },
        {
            "surfaceForm": "stress",
            "start": 10,
            "stop": 16,
            "candidates": [
                {
                    "score": 0.1679216436357857,
                    "wikiTitle": "Stress_(biological)",
                    "wikiId": 146072,
                    "attributes": ""
                },
                {
                    "score": 0.13064049689383125,
                    "wikiTitle": "Stress_(physics)",
                    "wikiId": 228107,
                    "attributes": ""
                },
                {
                    "score": 0.057278176391989205,
                    "wikiTitle": "Stress_(linguistics)",
                    "wikiId": 171782,
                    "attributes": "phonetics"
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Shear_stress",
                    "wikiId": 437619,
                    "attributes": "concept\tmechanics"
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Compressive_stress",
                    "wikiId": 424487,
                    "attributes": ""
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Stress_management",
                    "wikiId": 255475,
                    "attributes": ""
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Stress_fracture",
                    "wikiId": 1173689,
                    "attributes": "injury"
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Accent_(music)",
                    "wikiId": 713921,
                    "attributes": ""
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Test_anxiety",
                    "wikiId": 8146278,
                    "attributes": ""
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Workplace_stress",
                    "wikiId": 14549668,
                    "attributes": ""
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Stress_hormone",
                    "wikiId": 5235623,
                    "attributes": ""
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Posttraumatic_stress_disorder",
                    "wikiId": 82974,
                    "attributes": "disorder\tpersonnel\tveteran"
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Muri_(Japanese_term)",
                    "wikiId": 10417992,
                    "attributes": "concept\tterms"
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Residual_stress",
                    "wikiId": 1873277,
                    "attributes": "mechanics"
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Chronic_stress",
                    "wikiId": 11758262,
                    "attributes": ""
                },
                {
                    "score": 0.04955074485218414,
                    "wikiTitle": "Dukkha",
                    "wikiId": 8702,
                    "attributes": "words\tterms\tconcept"
                }
            ]
        },
        {
            "surfaceForm": "TRH",
            "start": 49,
            "stop": 52,
            "candidates": [
                {
                    "score": 0.8801919286084104,
                    "wikiTitle": "Thyrotropin-releasing_hormone",
                    "wikiId": 183993,
                    "attributes": "hormone"
                },
                {
                    "score": 0.11980807139158968,
                    "wikiTitle": "Royal_Highness",
                    "wikiId": 651616,
                    "attributes": "style"
                }
            ]
        },
        {
            "surfaceForm": "TSH",
            "start": 57,
            "stop": 60,
            "candidates": [
                {
                    "score": 0.8279239651267767,
                    "wikiTitle": "Thyroid-stimulating_hormone",
                    "wikiId": 330361,
                    "attributes": "hormone"
                },
                {
                    "score": 0.17207603487322334,
                    "wikiTitle": "Serene_Highness",
                    "wikiId": 1106074,
                    "attributes": "style"
                }
            ]
        },
        {
            "surfaceForm": "secretion",
            "start": 61,
            "stop": 70,
            "candidates": [
                {
                    "score": 0.7487644221483419,
                    "wikiTitle": "Secretion",
                    "wikiId": 534001,
                    "attributes": ""
                },
                {
                    "score": 0.25123557785165807,
                    "wikiTitle": "Exocytosis",
                    "wikiId": 73614,
                    "attributes": "loanword"
                }
            ]
        }
    ]
}
```
You can use the wikiTitle or wikiIds to do queries into Wikipedia using their API, e.g.

https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&pageids=1312500

https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&titles=Digestive_system

Note that the version of Wikipedia used in the wikifier is rather old, so ids/titles may have changed. However, the few that I've spot checked seem to be the same.

