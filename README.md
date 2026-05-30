Sokoban alkalmazas

A projekt celja egy sokoban jatek alkalmazas elkeszitese JavaFX-ben.

Fo funkciok:
- Sajat json alapu palya adatbazis.
    - torles
    - bovites .xsb formatumu file beolvasasaval
    - kiiras .xsb
- palya validifikacio - pontosan 1 jatekos van a palyan; cel mezok szama megegyezik a dobozok szamaval
- .xsb validifikacio - csak megengedett karaktereket tartalmaz,  

Teszt esetek:
- jatek logika
- palya validifikacio
- palya kiirasa es beolvasasa konzisztens.

xsb referencia 1.: http://sokoban.org/about_sokoban.php
xsb referencia 2.: src/main/java/io/github/zoldseges/persistence/xsb-format.md