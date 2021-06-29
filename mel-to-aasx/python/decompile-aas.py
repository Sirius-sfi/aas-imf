import datetime
from pathlib import Path  # Used for easier handling of auxiliary file's local path

import pyecma376_2  # The base library for Open Packaging Specifications. We will use the OPCCoreProperties class.
from aas import model
from aas.adapter import aasx

new_object_store: model.DictObjectStore[model.Identifiable] = model.DictObjectStore()
new_file_store = aasx.DictSupplementaryFileContainer()

with aasx.AASXReader("C:/Users/ofschha3/git/github/Sirius-sfi/aas-imf/deliveryPrototype/DeliveryPrototype/ImfTest.aasx") as reader:
    # Read all contained AAS objects and all referenced auxiliary files
    # In contrast to the AASX Package Explorer, we are not limited to a single XML part in the package, but instead we
    # will read the contents of all included JSON and XML parts into the ObjectStore
    reader.read_into(object_store=new_object_store,
                     file_store=new_file_store)

    # We can also read the meta data
    new_meta_data = reader.get_core_properties()

    # We could also read the thumbnail image, using `reader.get_thumbnail()`


