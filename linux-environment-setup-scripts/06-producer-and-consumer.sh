#! /bin/bash

sudo mkdir /mestrado
sudo mkdir /mestrado/statistics
sudo mkdir /mestrado/statistics/producer/
sudo mkdir /mestrado/statistics/consumer/
sudo mkdir /mestrado/indir/

sudo mkdir /mestrado/CMUSphinxResources/
sudo mkdir /mestrado/CMUSphinxResources/AcousticModel/
sudo mkdir /mestrado/CMUSphinxResources/AcousticModel/en-70k-0.2.lm
sudo mkdir /mestrado/CMUSphinxResources/AcousticModel/en-70k-0.2.lm/cmusphinx-en-us-5.2
sudo mkdir /mestrado/CMUSphinxResources/LexiconModel/
sudo mkdir /mestrado/CMUSphinxResources/LanguageModel/
sudo mkdir /mestrado/CMUSphinxResources/LanguageModel/en-70k-0.2.lm/

cd /mestrado/indir/
sudo wget --no-check-certificate -r 'https://docs.google.com/uc?export=download&id=1E5OnsG1ojb8wk-PQr6XXpOhmjoNunHym' -O audio.wav


cd /mestrado
sudo wget --no-check-certificate -r 'https://docs.google.com/uc?export=download&id=1y0ToAO_C5GvFvk9Lq6YFDvAaTt09xDNC' -O produtor-0.0.1.jar
sudo wget --no-check-certificate -r 'https://docs.google.com/uc?export=download&id=1lVvnkWyY9-A09-rWSN7csz356tcJ0i_F' -O processador-0.0.1.jar

cd /mestrado/CMUSphinxResources/AcousticModel/en-70k-0.2.lm/cmusphinx-en-us-5.2
sudo wget --no-check-certificate -r 'https://docs.google.com/uc?export=download&id=1ALxzPyknOQjkIJI0YVpCqtoBhPdcb7yl' -O acousticmodel.rar
sudo unrar x acousticmodel.rar
sudo rm acousticmodel.rar

cd /mestrado/CMUSphinxResources/LexiconModel/
sudo wget --no-check-certificate -r 'https://docs.google.com/uc?export=download&id=1fVR6m0uml5iW_sUwzThbgawIszdWBf5r' -O cmudict-en-us.dict

cd /mestrado/CMUSphinxResources/LanguageModel/en-70k-0.2.lm/
sudo wget --no-check-certificate -r 'https://docs.google.com/uc?export=download&id=1fiYNPYKwQZytrUWw1WUE3uhJlUfsGMI4' -O en-70k-0.2.lm

sudo apt-get install -y sphinxbase-utils

sudo sphinx_lm_convert -i en-70k-0.2.lm -o en-70k-0.2.lm.bin