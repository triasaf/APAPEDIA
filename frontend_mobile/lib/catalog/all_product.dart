import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:frontend_mobile/catalog/detail_product.dart';
import 'package:frontend_mobile/models/catalog.dart';
import 'package:frontend_mobile/service/catalog_service.dart';
import 'package:frontend_mobile/widget/drawer.dart';
import 'package:intl/intl.dart';

class CatalogListWidget extends StatefulWidget {
  final CatalogService catalogService;

  const CatalogListWidget({super.key, required this.catalogService});

  @override
  State<CatalogListWidget> createState() => _CatalogListWidgetState();
}

class _CatalogListWidgetState extends State<CatalogListWidget> {
  late Future<List<Result>> catalogs;
  late Future<List<CategoryId>> categorys;
  List<Result> allCatalogs = []; // Original list of catalogs
  int? startPrice;
  int? endPrice;
  String? categoryName;
  TextEditingController searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    catalogs = widget.catalogService.getAllCatalogs();
    categorys = widget.catalogService.getAllCategories();
    catalogs.then((value) {
      allCatalogs = value; // Save the original list when it's first loaded
    });
  }

  Future<Uint8List> getImage(String catalogId) async {
    try {
      return await widget.catalogService.getImage(catalogId);
    } catch (e) {
      // Handle error
      return Uint8List(0);
    }
  }

  List<Result> findIntersection<Result>(
      List<Result> list1, List<Result> list2) {
    return list1
        .where((element) => list2.any((other) => element == other))
        .toList();
  }

  Future<void> _filterCatalogs() async {
    try {
      List<Result> filteredCatalogs = allCatalogs;

      // Apply category filter
      if (categoryName != null && categoryName != 'all') {
        filteredCatalogs = filteredCatalogs
            .where((catalog) => catalog.categoryId.name == categoryName)
            .toList();
      }

      // Apply price range filter
      if (startPrice != null) {
        filteredCatalogs = filteredCatalogs
            .where((catalog) => catalog.price >= startPrice!)
            .toList();
      }

      if (endPrice != null) {
        filteredCatalogs = filteredCatalogs
            .where((catalog) => catalog.price <= endPrice!)
            .toList();
      }

      var filteredResults = await widget.catalogService.getFilteredProducts(
        category: categoryName ?? 'all',
        startPrice: startPrice,
        endPrice: endPrice,
      );

      var searchedResults = await widget.catalogService
          .getSearchedCatalogs(searchQuery: searchController.text);

      var finalResults = findIntersection(filteredResults, searchedResults);

      setState(() {
        catalogs = Future.value(finalResults);
      });
    } catch (e) {
      // Handle error
    }
  }

  final currencyFormatter =
      NumberFormat.currency(locale: 'id_ID', symbol: 'Rp', decimalDigits: 0);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Catalog List'),
        backgroundColor: Colors.blue,
      ),
      drawer: const Drawers(),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              controller: searchController,
              onChanged: (value) {
                // Call _filterCatalogs when the search field changes
                _filterCatalogs();
              },
              decoration: const InputDecoration(
                labelText: 'Search',
                suffixIcon: Icon(Icons.search),
                border: OutlineInputBorder(),
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              children: [
                FutureBuilder<List<CategoryId>>(
                  future: categorys,
                  builder: (context, categorySnapshot) {
                    if (categorySnapshot.hasData) {
                      return DropdownButton<String>(
                        value: categoryName,
                        onChanged: (String? value) {
                          setState(() {
                            categoryName = value;
                          });
                        },
                        items: [
                          'all',
                          ...categorySnapshot.data!
                              .map((category) => category.name)
                        ].map<DropdownMenuItem<String>>((String value) {
                          return DropdownMenuItem<String>(
                            value: value,
                            child: Text(value),
                          );
                        }).toList(),
                      );
                    } else if (categorySnapshot.hasError) {
                      return Text('Error: ${categorySnapshot.error}');
                    } else {
                      return const CircularProgressIndicator();
                    }
                  },
                ),
                const SizedBox(width: 16),
                TextField(
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(
                    labelText: 'Start Price',
                    border: OutlineInputBorder(),
                  ),
                  onChanged: (value) {
                    setState(() {
                      startPrice = int.tryParse(value);
                    });
                  },
                ),
                const SizedBox(width: 16),
                TextField(
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(
                    labelText: 'End Price',
                    border: OutlineInputBorder(),
                  ),
                  onChanged: (value) {
                    setState(() {
                      endPrice = int.tryParse(value);
                    });
                  },
                ),
                const SizedBox(width: 16),
                ElevatedButton(
                  onPressed: _filterCatalogs,
                  child: const Text('Apply Filters'),
                ),
              ],
            ),
          ),
          Expanded(
            child: FutureBuilder<List<Result>>(
              future: catalogs,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const CircularProgressIndicator();
                }
                if (snapshot.hasError) {
                  return Text('Error: ${snapshot.error}');
                }
                if (!snapshot.hasData || snapshot.data!.isEmpty) {
                  return const Center(child: Text('No products found'));
                }

                return ListView.builder(
                  itemCount: snapshot.data!.length,
                  itemBuilder: (context, index) {
                    var catalog = snapshot.data![index];
                    return Card(
                      elevation: 4.0,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12.0),
                      ),
                      child: ListTile(
                        title: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              catalog.productName,
                              style: const TextStyle(
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                                color: Colors.black,
                              ),
                            ),
                            const SizedBox(height: 8),
                            Text(
                              'Price: ${currencyFormatter.format(catalog.price)}',
                              style: const TextStyle(
                                color: Colors.blue,
                                fontSize: 14,
                              ),
                            ),
                          ],
                        ),
                        leading: FutureBuilder<Uint8List>(
                          future: getImage(catalog.id),
                          builder: (context, imageSnapshot) {
                            if (imageSnapshot.connectionState ==
                                    ConnectionState.done &&
                                imageSnapshot.hasData) {
                              return Image.memory(
                                imageSnapshot.data!,
                                width: 50,
                                height: 50,
                                fit: BoxFit.cover,
                              );
                            } else if (imageSnapshot.hasError) {
                              return Text(
                                  'Error loading image: ${imageSnapshot.error}');
                            } else {
                              return const CircularProgressIndicator();
                            }
                          },
                        ),
                        trailing: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            ElevatedButton(
                              onPressed: () {
                                Navigator.pushReplacement(
                                  context,
                                  MaterialPageRoute(
                                    builder: (context) => CatalogDetailWidget(
                                      catalogService: widget.catalogService,
                                      catalogId: catalog.id,
                                    ),
                                  ),
                                );
                              },
                              style: ElevatedButton.styleFrom(
                                backgroundColor: Colors.white,
                              ),
                              child: const Text('See Details'),
                            ),
                          ],
                        ),
                      ),
                    );
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
