-------------------------
HOW TO DOCUMENT THE TESTS
-------------------------

Each test should be documented as following:
- one directory/folder for one test
- in this directory one should be able to find the following information:
  - the complete transade script
  - the generated scala classes and report
  - a perfect copy of all source repositories used by the application during the transade script computation
  - a copy perfect of all target repositories used by the application BEFORE the transade script computation
  - a copy perfect of all target repositories used by the application AFTER the transade script computation


The directory structure will therefore be as such:
  tester name / test name / scripts / transade script
                                    / report script
                                    /scala / scala script 1
                                           / scala script 2
                                           / scala script ...
                                           / scala script z
                          / repositories / source / repository a
                                                  / repository b
                                                  / repository ...
                                                  / repository z
                                         / target / before / repository a
                                                           / repository b
                                                           / repository ...
                                                           / repository z
                                                  / after / repository a
                                                          / repository b
                                                          / repository ...
                                                          / repository z


In case an external private directory is required, create a directory called "yyyyy" or "zzzzz" at the same level of the
main/general "tests" directory.