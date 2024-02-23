# CryptoTax
A simple terminal based tax report generator for BTCMarkets that collates and processes the data from BTCMarkets' "Reports for Tax".
The report generated displays the total deposits, total withdawal, profit excluding fees, fees and losses.
Additionally, unaccounted sales and unrealised profits/losses are displayed, along with unaccounted purchases (yet to be sold).

## Installation

Download and extract.
Compile:
```bash
javac *.java
```
## Usage

```bash
java CryptoTax
```

The program is limited to 5 options, enter the corresponding number to access the option.

1) Upload all your BTCMarkets tax reports via the "Import Files" option.
2) Optionally set a financial year to limit the reporting range with the "Limit Reporting Range" option. e.g. 2021
3) Select the "View Report" option
