Dette prosjektet bygger og analyserer en stor graf basert på IMDb-data, hvor noder representerer skuespillere, og kanter representerer filmer de har spilt sammen i. 
Grafen inneholder rundt 100 000 noder og 5 millioner kanter, og jeg bruker den til å utforske ulike grafalgoritmer.

**Oppgaver i prosjektet:**

- Bygging av graf – Leser og strukturerer IMDb-data fra movies.tsv og actors.tsv for å konstruere en vektet, urettet graf.
- Antall noder og kanter – Beregner og verifiserer størrelsen på ulike grafer. 
- Finne korteste vei mellom skuespillere – Implementerer en algoritme for å finne den korteste stien mellom to skuespillere basert på felles filmer.
- "Chilleste vei" – Bruker vekting for å finne en rute gjennom høyt rangerte filmer, basert på IMDb-ratings.
- komponentanalyse – Identifiserer sammenhengende komponenter i grafen og teller deres størrelse.

