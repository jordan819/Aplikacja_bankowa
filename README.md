
<h1 align="center">Aplikacja bankowa</h1>

<!-- SPIS TREŚCI -->
<details>
  <summary>Spis treści</summary>
  <ol>
    <li>
      <a href="#o-projekcie">O projekcie</a>
      <ul>
        <li><a href="#wykorzystane-biblioteki">Wykorzystane biblioteki</a></li>
      </ul>
    </li>
    <li><a href="#uruchamianie">Uruchamianie</a></li>
    <li>
      <a href="#funkcjonalności">Funkcjonalności</a>
      <ul>
        <li>
          <a href="#zakładanie-konta">Zakładanie konta</a>
          <ul>
            <li><a href="#weryfikacja-konta">Weryfikacja konta</a></li>
          </ul>
        </li>
        <li><a href="#logowanie">Logowanie</a></li>
        <li><a href="#wpłata-i-wypłata-środków">Wpłata i wypłata środków</a></li>
        <li><a href="#przewalutowanie">Przewalutowanie</a></li>
        <li><a href="#przelew-środków">Przelew środków</a></li>
        <li><a href="#zaciągnięcie-i-spłata-pożyczki">Zaciągnięcie i spłata pożyczki</a></li>
        <li><a href="#dezaktywacja-konta">Dezaktywacja konta</a></li>
      </ul>
    </li>
  </ol>
</details>

## O projekcie
Aplikacja desktopowa, stworzona w języku Java, z wykorzystaniem technologii JavaFX. 
Program komunikuje się z bazą danych, w której zapisuje wszystkie dane dotyczące użytkowników, oraz ich kont bankowych.

Daje użytkownikowi możliwość utworzenia nowego konta bankowego, a po jego weryfikacji, zarządzanie nim. Udostępnione zostają mu podstawowe funkcjonalności kojarzące się
z obsługą konta bankwoego: zarządzanie saldem, przelewanie środków, przewalutowanie środków, czy też dezaktywacja konta.

### Wykorzystane biblioteki
* [activation](https://docs.oracle.com/javase/10/docs/api/java.activation-summary.html)
* [commons-lang3](https://commons.apache.org/proper/commons-lang/)
* [gson](https://github.com/google/gson)
* [java.awt](https://docs.oracle.com/javase/7/docs/api/java/awt/package-summary.html)
* [javax.mail](https://docs.oracle.com/javaee/7/api/javax/mail/package-summary.html)
* [postgresql](https://jdbc.postgresql.org/documentation/head/intro.html)

## Uruchamianie
Po pobraniu repozytorium należy dodać do projektu wszystkie wymienione wcześniej biblioteki. Znajdują się one w katalogu lib.

Program jest już gotowy do działania.

# Funkcjonalności

## Zakładanie konta
W celu utworzenia nowego konta, użytkownik musi podać następujące dane:
- Imię,
- Nazwisko,
- Adres email,
- Hasło (składające się z conajmniej 8 znaków).

Jeżeli adres email nie jest przypisany do innego użytkownika, utworzone zostanie nowe konto, z unikatowym numerem rachunku. Wszystkie dane zapisywane są do bazy,
a użytkownik otrzymuje na podany adres email kod wymagany do weryfikacji utworzonego konta.

### Weryfikacja konta
Użytkownik wprowadza swój adres email, oraz otrzymany na niego numer weryfikacyjny. Jeżeli dane są poprawne, konto zostaje doładowane kwotą 1000 PLN, oraz aktywowane.
Na adres email klienta wysyłany jest numer konta bankowego, wymagany do zalogowania się.

## Logowanie
Użytkownik podaje numer swojego rachunku, oraz hasło utworzone w procesie rejestracji. Po wypełnieniu obydwu pól, przycisk logowania staje się aktywny.
Po jego kliknięciu dane porównywane są z tymi przechowywanymi w bazie danych. W przypadku zgodności, użytkownik uzyskuje dostęp do konta.

## Wpłata i wypłata środków
Użytkownik ma możliwość szybkiego zasilenia swojego konta bezpośrednią wpłatą gotówki, bądź jej wypłaty. Przed wypłaceniem gotówki sprawdzane jest, czy stan konta pozwala
na podjęcie wybranej przez klienta sumy. W obydwu przypadkach podana kwota musi być liczbą podzielną przez 10.

## Przewalutowanie
Użytkownik może przechowywać swoje pieniądze w jednej z 4 dostępnych walut:

* PLN (domyślna),
* USD,
* EUR,
* GBP.

W dowolnej chwili może przewalutować swoje konto na jedną z nich. Po wybraniu interesującej go pozycji z listy, pobierany jest aktualny kurs wymiany walut,
obliczany nowy stan konta, który zostaje wyświetlony klientowi. Jeżeli potwierdzi swój wybór, saldo konta oraz informacja o jego walucie zostają zaktualizowane
i zapisane w bazie danych.

## Przelew środków
Użytkownik może przelać swoje pieniądze na dowolne aktywne konto zarejestrowane w banku. W tym celu podaje numer konta docelowego, oraz kwotę jaką chce przelać.
Jeżeli konto o podanym numerze istnieje i jest aktywne, a stan konta pozwala na przelanie wprowadzonej sumy, przelew jest realizowany. Pobierane są informacje
o walutach kont nadawcy i odbiorcy, po czym w razie potrzeby następuje przewalutowanie.

## Zaciągnięcie i spłata pożyczki
Użytkownik ma możliwość zaciągnięcia w banku pożyczki (pod warunkiem, że żadna nie jest już przypisana do jego konta). W tym celu podaje pożądaną kwotę, oraz termin
w jakim chce pożyczkę spłacić. Możliwe czasy jej trwania to:

* 3 miesiące,
* 6 miesięcy,
* 1 rok,
* 2 lata.

Im dłuższy okres klient wybierze, tym większe odsetki zostaną naliczone.

Gdy klient potwierdzi swój wybór, w bazie zapisywane są informacje o kwocie pozostałej do spłacenia, oraz terminie tej spłaty.

Spłacanie może odbywać się w dowolnej liczbie rat, przy użyciu dowolnej kwoty.

## Dezaktywacja konta
Jeżeli użytkownik nie chce już więcej korzystać z usług banku, ma możliwość dezaktywowania swojego konta. W takiej sytuacji, w bazie zapisywana jest informacja o tym,
co uniemożliwia zalogowanie się na konto. W dalszy, ciągu widnieją tam informacje o stanie konta i potencjalnych pożyczkach w momencie dezaktywacji.
