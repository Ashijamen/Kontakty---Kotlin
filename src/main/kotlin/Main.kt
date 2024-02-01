import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread
// Importuje potrzebne klasy do obsługi listy kontaktów i wielowątkowości.

data class Kontakt(val imie: String, val nazwisko: String, val numerTelefonu: String)
// Definiuje klasę danych Kontakt, która reprezentuje informacje o kontakcie.

class ListaKontaktow {
    private val kontakty = CopyOnWriteArrayList<Kontakt>()
    // Tworzy listę kontaktów zabezpieczoną na wypadek wielu wątków.

    @Synchronized //dostęp do metody dodajKontakt jest synchronizowany na poziomie obiektu ListaKontaktow
    fun dodajKontakt(imie: String, nazwisko: String, numerTelefonu: String) {
        val kontakt = Kontakt(imie, nazwisko, numerTelefonu)
        kontakty.add(kontakt)
//        println()
//        println("Dodano kontakt: $imie $nazwisko, Numer telefonu: $numerTelefonu")
    }
    // Metoda dodająca kontakt do listy, zabezpieczona przed dostępem wielu wątków.

    @Synchronized
    fun usunKontakt(imie: String, nazwisko: String) {
    //(val - niezmienna)
        val kontaktDoUsuniecia = kontakty.find { it.imie == imie && it.nazwisko == nazwisko }
        if (kontaktDoUsuniecia != null) {
            kontakty.remove(kontaktDoUsuniecia)
//            println("Usunięto kontakt: $imie $nazwisko")
        } else {
            println("Nie znaleziono kontaktu o podanych danych: $imie $nazwisko")
        }
    }
    // Metoda usuwająca kontakt z listy, zabezpieczona przed dostępem wielu wątków.

    @Synchronized
    fun wyswietlKontakty() {
        if (kontakty.isEmpty()) {
            println("Lista kontaktów jest pusta.")
        } else {
            println("Lista kontaktów:")
            for (kontakt in kontakty) {
                println("Imię: ${kontakt.imie}, Nazwisko: ${kontakt.nazwisko}, Numer telefonu: ${kontakt.numerTelefonu}")
            }
        }
    }
    // Metoda wyświetlająca listę kontaktów, zabezpieczona przed dostępem wielu wątków.
}

fun main() {
    val listaKontaktow = ListaKontaktow()

    // Dodanie trzech nowych osób
//    println("Kontakty")
    listaKontaktow.dodajKontakt("Anna", "Nowak", "12345678")
    listaKontaktow.dodajKontakt("Jan", "Kowalski", "87654321")
    listaKontaktow.dodajKontakt("Ewa", "Wiśniewska", "11112222")

    while (true) {
        println("\nMenu:")
        println("1. Dodaj kontakt")
        println("2. Usuń kontakt")
        println("3. Wyświetl kontakty")
        println("4. Wyjście")

        print("Wybierz opcję: ")
        when (val opcja = readLine()) {
            "1" -> {
                println("Podaj imię: ")
                val imie = readLine() ?: ""
                println("Podaj nazwisko: ")
                val nazwisko = readLine() ?: ""
                println("Podaj numer telefonu (9 cyfr): ")
                val numerTelefonu = readLine() ?: ""
                println("Pomyślnie dodano nowy kontakt")

                if (numerTelefonu.length == 9 && numerTelefonu.all { it.isDigit() }) {
                    thread {
                        listaKontaktow.dodajKontakt(imie, nazwisko, numerTelefonu)
                    }
                } else {
                    println("Nieprawidłowy numer telefonu. Dodawanie kontaktu przerwane.")
                }

            }
            "2" -> {
                println("Podaj imię kontaktu do usunięcia: ")
                val imie = readLine() ?: ""
                println("Podaj nazwisko kontaktu do usunięcia: ")
                val nazwisko = readLine() ?: ""
                println("Usunięto kontakt: $imie $nazwisko")

                //zostanie wykonane w nowym wątku
                thread {
                    listaKontaktow.usunKontakt(imie, nazwisko)
                }
            }
            "3" -> {
                listaKontaktow.wyswietlKontakty()
            }
            "4" -> {
                println("Do widzenia!")
                return
            }
            else -> println("Nieprawidłowa opcja. Spróbuj ponownie.")
        }
    }
}




// Pętla główna programu, obsługująca interakcję z użytkownikiem i wywołująca odpowiednie metody klasy ListaKontaktow.
