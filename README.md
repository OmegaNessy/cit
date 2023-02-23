# Crypto Investment Tool
This application was made to help developers invest their salaries on cryptoes.

This tool alows you to find:

  -Sorted list of all the cryptos, compared the normalization range (ex. GET localhost:8080/crypto/list)
  
  -Provide oldest/newest/min/max values for a requested crypto (ex. GET localhost:8080/crypto)
  
  -The crypto with the highest normalized range for aspecific day (ex. GET localhost:8080/crypto/normalized?day=2022-01-01)
  
  #QuickGuid
  
  Provide the app with valid {cryptoName}_values.csv. Put it in resourse/data folder
  Application will parse data and will be ready to process it.
  Start application and make your requests documanted on link 
