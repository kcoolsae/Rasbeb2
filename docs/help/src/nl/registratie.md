Leerlingen registreren bij de server
===
{% include _menu_nl.md %}

---
Leerlingen kunnen in groep geregistreerd worden bij de server[^1] door het uploaden van een Excel-bestand. Hoe dan ook moet eerst de *klas* van de
leerlingen zijn toegevoegd aan de school (zie onderaan).

### Structuur van het Excel-bestand

Lege lijnen en lijnen waarvan de eerste cel begint met # worden genegeerd. Elke andere lijn correspondeert
    met een leerling. Ze moeten verplicht de volgende kolomstructuur hebben:

* Kolom **A**: de **klas** van de leerling. Gebruik dezelfde afkorting waarmee je de klas aan de school hebt
    toegevoegd. 
* Kolom **B**: de **volledige naam** van de leerling. We suggereren dat je hier familienaam, komma, spatie, voornaam gebruikt,
  maar dit is niet verplicht
* Kolom **C**: het **gender** van de leerlingen - M, V of X. Dit wordt enkel gebruikt voor
  de internationale Bebras-statistieken. Je kan X gebruiken als je de privacy van de leerling wil beschermen.

Je hoeft niet alle leerlingen in één bestand te plaatsen of in één keer te uploaden. 
Hetzelfde bestand mag leerlingen bevatten uit verschillende klassen.

Je kan onderstaand [voorbeeldbestand](example.xlsx) downloaden.

![](example.png)

Leerlingen moeten elk schooljaar opnieuw worden geregistreerd.

### Bebras-ID en wachtwoord

Bij het uploaden van dit bestand zal de toepassing een *Bebras ID* en een *wachtwoord* genereren
voor de leerlingen. Die hebben ze nodig om in te loggen en aan de wedstrijd deel te nemen. De leerkracht
kan een Excel-bestand downloaden met voor alle leerlingen van de school hun Bebras-ID en hun wachtwoord.

Enkel de leerkracht kan het wachtwoord van een leerling aanpassen.

### Individuele registratie

Leerlingen kunnen ook individueel geregistreerd worden zonder dat 

###  Klassen

Leerlingen van een school worden gegroepeerd in klassen.

**TODO** documentatie aanvullen...


Klassen moeten elk schooljaar opnieuw worden aangemaakt.


#### Voetnoten
[^1]: Niet te verwarren met [Inschrijven bij een evenement](registratie.md) (**TODO** link aanpassen) 